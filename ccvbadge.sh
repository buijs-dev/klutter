TOTAL=80
COLOR=brightgreen
curl "https://img.shields.io/badge/coverage-$TOTAL-COLOR" > badge.svg
#gsutil  -h "Cache-Control: no-cache" cp badge.svg gs://$SOME_BACKET/$PROJECT_NAME/codcov.svg
#gsutil acl ch -u AllUsers:R gs://$SOME_BACKET/$PROJECT_NAME/codcov.svg
#REPLACE $SOME_BUCKET and $PROJECT_NAME