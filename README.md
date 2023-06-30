# Email Service API

A simple emailing service that lets you send emails via **Amazon Simple Email Service** and **MailGun.**

It uses abstraction between the two email service providers to ensure seamless emailing experience in the event of a failure in one of the service providers.

## Prerequisites

### Accounts
* AWS Amazon Account with configured User in the IAM
  * Required permission policies: **AmazonSESFullAccess**
* MailGun Account

### Verified Entities
Verify first the recipient email addresses in order to receive emails using the applications.
* For **AWS**, open the Amazon SES Console add the email addresses under Configuration > Verified Identities. Don't forget to check the email verification sent by aws after the configuration.
* Fail **MailGun**, add the email addresses under Sending > Domains. Click on one of your domains and the recipient list will show on the right side. Add the email addresses of the recipients and check the email confirmation of mailgun after configuration.

### Applications
* Intellij or other IDE
* Postman
* Java 17

## How to Use

### Local Setup
* Clone the repository
* Run ```mvn clean install``` inside the directory to resolve all dependencies in the ```pom.xml```
* Set up the aws configuration:
    * AWS_ACCESS_KEY_ID - Secret ID of the configured User in the IAM. Add this environment variable inside the run configuration of EmailServiceApplication
    * AWS_SECRET_KEY - Secret key of the configured User in the IAM. Add this environment variable inside the run configuration of EmailServiceApplication
    * Source Email
      * Open the application.yml inside the project and set your verified email under cloud.aws.source-email. This will appear as the source address of the sent emails.
    * AWS Region
      * Open the application.yml inside the project and set the region where the Amazon SES is configured under cloud.aws.region.static
  
* Set up the mailgun credentials by providing the following
  * Open the control panel of your mailgun account and go to Sending > Domains tab.
  * Click on one of the displayed domains then select API. Copy the api key and the domain name and set it in the application.yml under mail-gun.api-key and mail-gun.source-email respectively.
* Open the project in Intellij and run EmailServiceApplication.java. The application should be running on localhost:8080
  
### Email Service API
* Import the ```email_service_api.postman_collection.json``` into postman
* After importing the collection, set the environment variable  ```baseUrl```. If you are running on your local, you can set it to ```localhost:8080```. If you are running it on the deployed environment, use the URL that I provided in the email.
* In the Send Email API, set the correct values in the request body.
    * recipients - list of string containing the list of emails you want to send your email to.
    * cc - list of string containing the list of emails you want cc'ed.
    * bcc - list of string containing the list of emails you want bcc'ed.
    * subject - String containing the email subject
    * body - String containing the email body


## Results

The response consists of the following fields:
* status - status code that indicates if the request succeeded or failed. Possible values are 200, 400, 500.
* message - either SUCCESS or FAILED. If it is a bad request, this field should contain details about the incorrect fields in the request body.
* handler (opt) - it signifies which service handled the request successfully. Possible values are AMAZON SES and MAILGUN
* details (opt) - if it encounters an error in the email service providers, it shoud contain details about the exception that occurred


### 200 OK
After successfully hitting the API, the result should look like this

```json
{
    "status": 200,
    "message": "SUCCESS",
    "handler": "AMAZON SES"
}
```

### 500 Server Error
If the request failed on the email services, the result should look like this

```json
{
    "status": 500,
    "message": "FAILED",
    "handler": null
}
```

### 400 Bad Request
If the request is not valid (ex. required fields are null), the result should look like this

```json
{
  "status": 400,
  "message": "body should not be null",
  "handler": null
}
```