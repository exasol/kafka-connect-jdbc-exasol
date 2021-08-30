package com.exasol.connect.jdbc.dialect;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.ConnectException;

import io.confluent.connect.jdbc.dialect.DatabaseDialect;
import io.confluent.connect.jdbc.dialect.DatabaseDialectProvider.SubprotocolBasedProvider;
import io.confluent.connect.jdbc.dialect.DropOptions;
import io.confluent.connect.jdbc.dialect.GenericDatabaseDialect;
import io.confluent.connect.jdbc.sink.JdbcSinkConfig;
import io.confluent.connect.jdbc.sink.PreparedStatementBinder;
import io.confluent.connect.jdbc.sink.metadata.FieldsMetadata;
import io.confluent.connect.jdbc.sink.metadata.SchemaPair;
import io.confluent.connect.jdbc.sink.metadata.SinkRecordField;
import io.confluent.connect.jdbc.util.ColumnDefinition;
import io.confluent.connect.jdbc.util.ColumnId;
import io.confluent.connect.jdbc.util.ExpressionBuilder;
import io.confluent.connect.jdbc.util.IdentifierRules;
import io.confluent.connect.jdbc.util.TableId;
import io.confluent.connect.jdbc.util.TableDefinition;

/**
 * A {@link DatabaseDialect} for Exasol.
 */
public class ExasolDatabaseDialect extends GenericDatabaseDialect {

    /**
     * The provider for {@link ExasolDatabaseDialect}.
     */
    public static class Provider extends SubprotocolBasedProvider {
        public Provider() {
            super(ExasolDatabaseDialect.class.getSimpleName(), "exa");
        }

        @Override
        public DatabaseDialect create(AbstractConfig config) {
            return new ExasolDatabaseDialect(config);
        }
    }

    /**
     * Create a new dialect instance with the given connector configuration.
     *
     * @param config the connector configuration; may not be null
     */
    public ExasolDatabaseDialect(AbstractConfig config) {
        super(config, new IdentifierRules(".", "\"", "\""));
    }

    @Override
    public StatementBinder statementBinder(
        PreparedStatement statement,
        JdbcSinkConfig.PrimaryKeyMode pkMode,
        SchemaPair schemaPair,
        FieldsMetadata fieldsMetadata,
        TableDefinition tableDefinition,
        JdbcSinkConfig.InsertMode insertMode
    ) {
        return new PreparedStatementBinder(
            this,
            statement,
            pkMode,
            schemaPair,
            fieldsMetadata,
            tableDefinition,
            insertMode
        );
    }

    @Override
    public void bindField(
        PreparedStatement statement,
        int index,
        Schema schema,
        Object value,
        ColumnDefinition colDef
    ) throws SQLException {
        if (value == null) {
            statement.setObject(index, null);
        } else {
            boolean bound = maybeBindLogical(statement, index, schema, value);
            if (!bound) {
                bound = maybeBindPrimitive(statement, index, schema, value, colDef);
            }
            if (!bound) {
                throw new ConnectException("Unsupported source data type: " + schema.type());
            }
        }
    }

    protected boolean maybeBindPrimitive(
        PreparedStatement statement,
        int index,
        Schema schema,
        Object value,
        ColumnDefinition colDef
    ) throws SQLException {
        return super.maybeBindPrimitive(statement, index, schema, value);
    }

    @Override
    protected String getSqlType(SinkRecordField field) {
        if (field.schemaName() != null) {
            switch (field.schemaName()) {
            case Decimal.LOGICAL_NAME:
                return "DECIMAL(36," + field.schemaParameters().get(Decimal.SCALE_FIELD) + ")";
            case Date.LOGICAL_NAME:
                return "DATE";
            case Timestamp.LOGICAL_NAME:
                return "TIMESTAMP";
            default:
                // fall through to normal types
            }
        }
        switch (field.schemaType()) {
        case INT8:
            return "DECIMAL(3,0)";
        case INT16:
            return "DECIMAL(5,0)";
        case INT32:
            return "DECIMAL(10,0)";
        case INT64:
            return "DECIMAL(19,0)";
        case FLOAT32:
            return "FLOAT";
        case FLOAT64:
            return "DOUBLE";
        case BOOLEAN:
            return "BOOLEAN";
        case STRING:
            return "CLOB";
        default:
            return super.getSqlType(field);
        }
    }

    @Override
    public String buildDropTableStatement(TableId table, DropOptions options) {
        ExpressionBuilder builder = expressionBuilder();

        builder.append("DROP TABLE");
        if (options.ifExists()) {
            builder.append(" IF EXISTS");
        }
        builder.append(" " + table);
        if (options.cascade()) {
            builder.append(" CASCADE CONSTRAINTS");
        }
        return builder.toString();
    }

    @Override
    public List<String> buildAlterTable(TableId table, Collection<SinkRecordField> fields) {
        final List<String> queries = new ArrayList<>(fields.size());
        for (SinkRecordField field : fields) {
            queries.addAll(super.buildAlterTable(table, Collections.singleton(field)));
        }
        return queries;
    }

    @Override
    public String buildUpsertQueryStatement(TableId table, Collection<ColumnId> keyColumns,
            Collection<ColumnId> nonKeyColumns) {
        ExpressionBuilder builder = expressionBuilder();
        builder.append("MERGE INTO ");
        builder.append(table);
        builder.append(" AS target USING (SELECT ");
        builder.appendList().delimitedBy(", ").transformedBy(ExpressionBuilder.columnNamesWithPrefix("? AS "))
                .of(keyColumns, nonKeyColumns);
        builder.append(") AS incoming ON (");
        builder.appendList().delimitedBy(" AND ").transformedBy(this::transformAs).of(keyColumns);
        builder.append(")");
        if (nonKeyColumns != null && !nonKeyColumns.isEmpty()) {
            builder.append(" WHEN MATCHED THEN UPDATE SET ");
            builder.appendList().delimitedBy(",").transformedBy(this::transformUpdate).of(nonKeyColumns);
        }
        builder.append(" WHEN NOT MATCHED THEN INSERT (");
        builder.appendList().delimitedBy(",").transformedBy(ExpressionBuilder.columnNames()).of(nonKeyColumns,
                keyColumns);
        builder.append(") VALUES (");
        builder.appendList().delimitedBy(",").transformedBy(ExpressionBuilder.columnNamesWithPrefix("incoming."))
                .of(nonKeyColumns, keyColumns);
        builder.append(")");
        return builder.toString();
    }

    private void transformAs(ExpressionBuilder builder, ColumnId col) {
        builder.append("target.").appendIdentifierQuoted(col.name()).append("=incoming.")
                .appendIdentifierQuoted(col.name());
    }

    private void transformUpdate(ExpressionBuilder builder, ColumnId col) {
        builder.appendIdentifierQuoted(col.name()).append("=incoming.").appendIdentifierQuoted(col.name());
    }

}
