# Email Service API

A simple emailing service that lets you send emails via **Sendgrid** or **MailGun.**

It uses abstraction between the two email service providers to ensure seamless emailing experience in the event of a failure in one of the service providers.

## Prerequisites

### Required Accounts
* SendGrid
* MailGun

### Verified Entities
Verify first the recipient/sender email addresses in order to receive or send emails using the email service providers.
* For **Sendgrid**, verify the sender identity by enrolling your email address in this [link](https://app.sendgrid.com/settings/sender_auth/senders/new)
* Fail **MailGun**, add the email addresses under Sending > Domains. Click on one of your domains and the recipient list will show on the right side. Add the email addresses of the recipients and check the email confirmation of mailgun after configuration.

### Applications
* Intellij or other IDE
* Postman
* Java 17

## How to Use

### Local Setup
* Clone the repository
* Run ```mvn clean install``` inside the directory to resolve all dependencies in the ```pom.xml```
* Set up the sendgrid configuration:
    * API Key - Create an api key using the [link](https://app.sendgrid.com/settings/api_keys). Then save the value under sendgrid.bearer in application.yml
    * Source Email - Save the email address of the verified sender identity under sendgrid.source-email in application.yml 
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
* handler (opt) - it signifies which service handled the request successfully. Possible values are SENDGRID and MAILGUN
* details (opt) - if it encounters an error in the email service providers, it shoud contain details about the exception that occurred


### 200 OK
After successfully hitting the API, the result should look like this

```json
{
    "status": 200,
    "message": "SUCCESS",
    "handler": "SENDGRID"
}
```

### 5xx Server Error
If the request failed on the email services, the result should look like this

```json
{
    "status": 500,
    "message": "FAILED",
    "details": "sample details about the exception"
}
```

### 4xx Client Error
If the request is not valid (ex. required fields are null), the result should look like this

```json
{
  "status": 400,
  "message": "body should not be null"
}
```