#!/bin/bash

function topic_list()
{
  $kafka_dir/bin/kafka-topics.sh --list --zookeeper $zookeeper_broker
}

function topic_count()
{
  $kafka_dir/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list $kafka_broker --topic $1 --time -1 --offsets 1 | awk -F  ":" '{sum += $3} END {print sum}'
}

function topic_create()
{
  $kafka_dir/bin/kafka-topics.sh --create --zookeeper $zookeeper_broker --replication-factor 1 --partitions 1 --topic $1

}

function kafka_start()
{
  gnome-terminal -- $kafka_dir/bin/zookeeper-server-start.sh $kafka_dir/config/zookeeper.properties
  sleep 3
  gnome-terminal -- $kafka_dir/bin/kafka-server-start.sh $kafka_dir/config/server.properties
}

function kafka_isrunning()
{
  ps aux | grep kafka
}

kafka_dir="/home/paul/projects/kafka/binaries/kafka_2.12-2.2.0"
zookeeper_broker="localhost:2181"
kafka_broker="localhost:9092"

echo "type kafka command (write help to list possible commands)"
while IFS= read -r line; do
  cmd="$(cut -d' ' -f1 <<<$line)"
  param1="$(cut -d' ' -f2 <<<$line)"

  if [ "$cmd" = "topic.list" ]; then 
    topic_list
  elif [ "$cmd" = "topic.count" ]; then 
    topic_count $param1
  elif [ "$cmd" = "topic.create" ]; then 
    topic_create $param1
  elif [ "$cmd" = "kafka.start" ]; then 
    kafka_start
  elif [ "$cmd" = "kafka.isrunning" ]; then 
    kafka_isrunning
  elif [ "$cmd" = "test" ]; then 
    echo
  else
    printf "\nlist of commands:
kafka.start\t start kafka
kafka.isrunning\t list process of kafka
topic.list\t list of topics
topic.create\t create the specified topic
\t\t param1: topic name
topic.count\t count messages in the topic\n"
  fi

  printf "\ntype kafka command (write help to list possible commands)"
done


