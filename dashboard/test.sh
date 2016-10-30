read -p "Enter your username, or email for a migrated account:"
username=$REPLY
read -sp "Enter your password:"
password=$REPLY
echo
data='{"agent":{"name":"Minecraft","version":1},"username":"'"$username"'","password":"'"$password"'","clientToken":"1","requestUser":true}'
output="$(curl -H "Content-Type: application/json" -X POST -d "${data}" https://authserver.mojang.com/authenticate)"
echo $output