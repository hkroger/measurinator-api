sbt "deploy node2" &
sbt "deploy node3" &
sbt "deploy node4" &
wait

ssh node2 sudo systemctl restart measurinator-api
sleep 5
ssh node3 sudo systemctl restart measurinator-api
sleep 5
ssh node4 sudo systemctl restart measurinator-api
echo All done.
