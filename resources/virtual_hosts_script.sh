# removing the dirs and symlinks
sudo rm -rf /var/www/bitpieces.com
sudo rm -rf /var/www/test.bitpieces.com

# making the correct dirs
sudo mkdir -p /var/www/bitpieces.com
sudo mkdir -p /var/www/test.bitpieces.com

# Now make the symlinks
sudo ln -s ~/git/bitpieces/resources/web /var/www/bitpieces.com/public_html
sudo ln -s ~/git/bitpieces/resources/testweb /var/www/test.bitpieces.com/public_html

sudo chown -R $USER:$USER /var/www/bitpieces.com/public_html
sudo chown -R $USER:$USER /var/www/test.bitpieces.com/public_html

sudo chown -R $USER:$USER ~/git/bitpieces/resources/web
sudo chown -R $USER:$USER ~/git/bitpieces/resources/testweb

sudo chmod -R 755 /var/www

sudo rm /etc/apache2/sites-available/bitpieces.com.conf
sudo rm /etc/apache2/sites-available/test.bitpieces.com.conf



MAIN_CONF="<VirtualHost *:80>
    ServerAdmin admin@bitpieces.com
    ServerName bitpieces.com
    ServerAlias www.bitpieces.com
    DocumentRoot /var/www/bitpieces.com/public_html
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
    Require all granted
</VirtualHost>
"

echo "$MAIN_CONF" | sudo tee -a /etc/apache2/sites-available/bitpieces.com.conf

TEST_CONF="<VirtualHost *:80>
    ServerAdmin admin@test.bitpieces.com
    ServerName test.bitpieces.com
    ServerAlias www.test.bitpieces.com
    DocumentRoot /var/www/test.bitpieces.com/public_html
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
    Require all granted
</VirtualHost>
"

echo "$TEST_CONF" | sudo tee -a /etc/apache2/sites-available/test.bitpieces.com.conf
cd /etc/apache2/sites-available/
sudo a2ensite bitpieces.com.conf
sudo a2ensite test.bitpieces.com.conf

sudo service apache2 restart

cd ~/git/bitpieces/resources

# may need to put these in host file
#sudo vim /etc/hosts
#111.111.111.111 bitpieces.com
#111.111.111.111 test.bitpieces.com
