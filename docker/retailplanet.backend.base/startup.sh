#!/bin/sh

# Set KAFKA_GROUP_ID environment variable correctly
function createAndSetKafkaGroupID
{
  kafkaGroupIDFile=".kafkaGroupID"

  if [ -f $kafkaGroupIDFile ]
  then
    # Read existing file
    export KAFKA_GROUP_ID=$(cat $kafkaGroupIDFile)
  else
    # Create new file if not exists.
    # The ID will be the current container creation timestamp
    kafkaGroupID=$(date +%s%3N)
    echo $kafkaGroupID > $kafkaGroupIDFile
    export KAFKA_GROUP_ID=$kafkaGroupID
  fi

  echo "KAFKA_GROUP_ID set to $KAFKA_GROUP_ID"
}

createAndSetKafkaGroupID

# Run application with given set of environment variables
$STARTUP_CMD