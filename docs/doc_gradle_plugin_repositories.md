# Klutter Project Repository Management

Private repositories can be added to the project in the plugin configuration.
Only maven repositories are supported at this time. The url and credentials are loaded
from the .yaml files in the klutter module, by specifying the key and using the 
appropiate getter.

There are 3 getters:
- local(key: String)
- shared(key: String)
- secret(key: String)

Local does a lookup for the key in the klutter.yaml, shared finds the key in klutter-local.yaml
and secret does a search in klutter-secrets.yaml. If the the key does not exist in the yaml file
then a KlutterConfigException is thrown.

**Important**: Repository management is a work in progress. Right now the repository is only added to the
android root gradle file. It will be expanded to other klutter modules.

**Examples**

Configure a repository without credentials by looking up the value of <i>key private.repo.url</i> 
in the klutter-secrets.yaml file:

```kotlin

klutter {
    
    repositories {
        maven {
            url = secret("private.repo.url")
        }
    }

}

```


<br/>

Configure a repository with credentials by looking up the url in the klutter-secrets.yaml
and the credentials in the klutter-local.yaml file:

```kotlin

klutter {
    
    repositories {
        maven {
            url = secrets("private.repo.url")
            username = local("private.repo.username")
            password = local("private.repo.password")
        }
    }

}

```