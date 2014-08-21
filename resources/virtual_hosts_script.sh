# removing the dirs and symlinks
sudo rm -rf /var/www/html/bitpieces.com
sudo rm -rf /var/www/html/test.bitpieces.com

# making the correct dirs
sudo mkdir -p /var/www/html/bitpieces.com
sudo mkdir -p /var/www/html/test.bitpieces.com

# Now make the symlinks
sudo ln -s ~/git/bitpieces/resources/web /var/www/html/bitpieces.com/public_html
sudo ln -s ~/git/bitpieces/resources/testweb /var/www/html/test.bitpieces.com/public_html

sudo chown -R $USER:$USER /var/www/html/bitpieces.com/public_html
sudo chown -R $USER:$USER /var/www/html/test.bitpieces.com/public_html

sudo chown -R $USER:$USER ~/git/bitpieces/resources/web
sudo chown -R $USER:$USER ~/git/bitpieces/resources/testweb

sudo chmod -R 755 /var/www

sudo rm /etc/apache2/sites-available/bitpieces.com.conf
sudo rm /etc/apache2/sites-available/test.bitpieces.com.conf



MAIN_CONF="<VirtualHost *:80>
    ServerAdmin admin@bitpieces.com
    ServerName bitpieces.com
    ServerAlias www.bitpieces.com
    DocumentRoot /var/www/html/bitpieces.com/public_html
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
    SSLEngine on
    SSLProtocol all
    SSLCertificateFile /home/tyler/public.crt
    SSLCertificateKeyFile /home/tyler/privatekey.key
    SSLCACertificateFile /home/tyler/intermediate.crt
     # <Directory /var/www/html/bitpieces.com/public_html>
     # Options Indexes FollowSymLinks
     #   Require all granted
    #Directory>
</VirtualHost>
"

echo "$MAIN_CONF" | sudo tee -a /etc/apache2/sites-available/bitpieces.com.conf

TEST_CONF="<VirtualHost *:80>
    ServerAdmin admin@test.bitpieces.com
    ServerName test.bitpieces.com
    ServerAlias www.test.bitpieces.com
    DocumentRoot /var/www/html/test.bitpieces.com/public_html
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
      #<Directory /var/www/html/test.bitpieces.com/public_html>
      #Options Indexes FollowSymLinks
      #  Require all granted
    #</Directory>
</VirtualHost>
"

echo "$TEST_CONF" | sudo tee -a /etc/apache2/sites-available/test.bitpieces.com.conf
cd /etc/apache2/sites-available/
sudo a2enmod rewrite
sudo a2enmod ssl
sudo a2ensite bitpieces.com.conf
sudo a2ensite test.bitpieces.com.conf
sudo service apache2 restart
sudo a2ensite /etc/apache2/sites-available/default-ssl.conf
cd ~/git/bitpieces/resources

# may need to put these in host file
#sudo vim /etc/hosts
#111.111.111.111 bitpieces.com
#111.111.111.111 test.bitpieces.com
