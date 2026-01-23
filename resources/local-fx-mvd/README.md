## Local environment testing

This docker-compose.yaml provides you a minimal environment for testing a pair of factory-edc's against each other. 

This also includes an issuer service, which acts as a trusted issuer in this regard. Both edc's are also accompanied by 
their respective identity hubs, which will act as their identity wallets. 

In order to start the environment, simply run 

```
docker compose up
```

Then please use the attached Bruno collection. There you should first run the requests of the ´identities´ folder. 
After you have completed all required steps, the provider and the consumer identity are onboarded in your own dataspace
and ready to interact with each other. 

Now you are ready to perform a simple contract negotiation and data transfer between these two actors. 

Be sure to also read the documentation that is attached to the folders in the Bruno collection. You may also want to 
check the pre- and postrequest scripts of many requests, because they may give you further insights. 