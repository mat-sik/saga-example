#!/bin/bash

# Function to check if a RabbitMQ node is responsive
wait_for_node() {
  NODE=$1
  echo "Waiting for $NODE to start..."

  while true; do
    # Run the ping command without interactive flags and capture the result
    if docker exec -it $NODE rabbitmqctl status | grep -q "Error:"; then
      echo "Waiting for $NODE..."
      sleep 5
    else
      echo "$NODE is running."
      break
    fi
  done
}

# Function to join a RabbitMQ node to the cluster
join_cluster() {
  NODE=$1
  docker exec -it $NODE sh -c "
    rabbitmqctl stop_app;
    rabbitmqctl reset;
    rabbitmqctl join_cluster rabbit@rabbit-1;
    rabbitmqctl start_app;
  "
}

# For some reason if I run docker exec -it on container before it is fully 
# started, the container crashes
sleep 10

# Wait for nodes
wait_for_node rabbit-1
wait_for_node rabbit-2
wait_for_node rabbit-3

# Connect rabbit-2 to the cluster
join_cluster rabbit-2

# Connect rabbit-3 to the cluster
join_cluster rabbit-3

# Verify cluster status
docker exec -it rabbit-1 rabbitmqctl cluster_status

