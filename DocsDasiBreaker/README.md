#install postgresql step1
echo 'deb http://apt.postgresql.org/pub/repos/apt/ xenial-pgdg main' >> /etc/apt/sources.list.d/pgdg.list
#install postgresql step2
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
#install postgresql step3
sudo apt-get update
sudo apt-get install postgresql-10
#for login in postgre
sudo -u postgres psql
