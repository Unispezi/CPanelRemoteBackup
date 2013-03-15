CPanelRemoteBackup
==================

(see https://github.com/Unispezi/CPanelRemoteBackup/wiki/Home/ for most current version of this file)

# What is it?
This is CPanelRemoteBackup, a tool to backup web sites which use cPanel as their administrative interface.
 
# What does it do?
CPanelRemoteBackup will start a full backup in cPanel. Once the backup is finished, it will be downloaded, verified, and deleted on the server.

# Why should I use this?
* Runs on any machine
* Can easily be started at regular intervals by any scheduler (CRON, Windows Scheduling Tools)
* Setup free (if Java is available), no hassle with setting up your own FTP server where cPanel can put the backup file. Just run the tool, wait, and find the backup file on your machine
* Will not eat up storage space on the server because the backups are deleted when they are downloaded

# Requirements

For this to work, you need 
* a local Java installation
* Username and password to log in to the cPanel web interface and to the FTP server of your web site
* Sufficient free space in the home directory of your web site (cPanel will put the backup there)
* Sufficient free space in the output directory to hold the backup

# Usage
Example:  
java -jar CPanelRemoteBackup-1.0.jar -user someusername -password somepassword mysite.mydomain.com c:/backups 

Will log on to the given homepage, trigger a backup, and put it in c:\backups .

Output will look something like this:

Starting CPanel backup  
Polling for backup we just started..: backup-5.6.2012_09-01-42_mysite.tar.gz  
Polling for backup file size to become stable....: 352024131 bytes  
Downloading backup-5.6.2012_09-01-42_mysite.tar.gz to c:\backups\backup-5.6.2012_09-01-42_mysite.tar.gz  
Downloaded 352024131 bytes successfully  
Verifying downloaded file ...OK  
Deleting backup-5.6.2012_15-00-25_kadampak.tar.gz on server  
Done.  

# Further help
Run  
java -jar CPanelRemoteBackup-1.0.jar -help  
for a list of options

# How to build
## You'll need 

* Java Development Kit (JDK)
* maven

Then just run
mvn package

In target you'll find an uber-jar, i.e. a JAR which contains the application logic plus all libraries it relies on.

# License

CPanelRemoteBackup may be used under the MIT license:

  Copyright (C) 2012 Carsten Lergenmüller

    Permission is hereby granted, free of charge, to any person obtaining a copy of this 
    software and associated documentation files (the "Software"), to deal in the Software 
    without restriction, including without limitation the rights to use, copy, modify, 
    merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
    permit persons to whom the Software is furnished to do so, subject to the following 
    conditions:

        The above copyright notice and this permission notice shall be included in all 
        copies or substantial portions of the Software.
        
        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
