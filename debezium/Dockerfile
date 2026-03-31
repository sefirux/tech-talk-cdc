FROM debezium/connect:2.6

# The base debezium/connect image includes only source connectors.
# The JDBC Sink connector is a separate plugin needed to write from Kafka into a database.
# We download it from Maven and place it in the Kafka Connect plugin path.
RUN curl -fsSL https://repo1.maven.org/maven2/io/debezium/debezium-connector-jdbc/2.6.0.Final/debezium-connector-jdbc-2.6.0.Final-plugin.tar.gz \
    | tar -xz -C /kafka/connect/
