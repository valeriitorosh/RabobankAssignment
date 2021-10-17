mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t valeriitorosh/rabobank-assignment-api .