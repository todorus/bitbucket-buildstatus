# bitbucket-buildstatus
Jenkins plugin to use the BitBucket buildstatus API

# Installation
1. Download the latest hpi from the releases page https://github.com/todorus/bitbucket-buildstatus/releases
2. Go to Manage Jenkins > Manage Plugins
3. Click the "Advanced" tab
4. You can upload the hpi file under "Upload Plugin"


# Setup
1. Go to Manage Jenkins > Configure system
2. Fill out the Bitbucket BuildStatus fields with the username and password for the Bitbucket user you want Jenkins to use
3. Go to your Job > Configure
4. Fill out the Bitbucket BuildStatus fields for the repositiory you want to post statuses for