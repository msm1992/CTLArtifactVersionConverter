# CTLArtifactVersionConverter

## api.json Conversion
If you are using the endpoint api.json converter endpoint, please add the following property to the deployment.toml file of APIM 4.2.0 pack :
```agsl
[apim.policy]
enable_api_level_policies = true
```