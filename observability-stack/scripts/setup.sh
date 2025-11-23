#!/bin/bash

# Create external network if not exists
docker network create crud 2>/dev/null || true

# Start the observability stack
docker-compose up -d

echo "Observability Stack Started!"
echo ""
echo "Access URLs:"
echo "Grafana: http://localhost:3000 (admin/admin)"
echo "Prometheus: http://localhost:9090"
echo "Kibana: http://localhost:5601"
# echo "Jaeger: http://localhost:16686"
# echo "Kiali: http://localhost:20001"
echo "cAdvisor: http://localhost:8081"