# Configuration management in Klutter

Klutter has it's own way of managing project configuration. Here we talk about:
1. [Underlaying principles of Klutter configuration management](#Underlaying-principles-of-Klutter-configuration-management)
2. [Brief summary of usage](#Brief-summary-of-usage)


## Underlaying principles of Klutter configuration management
Gradle offers multiple ways of management like included builds (buildSrc) and composed builds. 
There are some benefits of using this, typesafety being one of the most important. The downside is you 
can not always use these typesafe classes and you are forced to include all sub modules in the Gradle build. 
The main purpose of Klutter is to connect different technologies/ecosystems. Instead of trying to 
connect these distinct ecosystems directly it makes sense to keep them intact but feed them the same
configuration. 

Klutter succeeds in doing this by acting as a facade for Flutter and KMP. The main requirements 
for configuration management through Klutter are as follows:

1. Single source of truth.
2. Easy to use.
3. Easy to maintain.

### Single source of truth
Klutter should provide all configuration information for the entire project in a single source.
(Sub)modules should get the information the need from the single Klutter source.

Why? Another principle is DRY: 'Don't repeat yourself'. To build an app with Flutter and KMP means having dependencies 
on a lot of different locations. Even the simplest form of a Klutter app will have Kotlin dependencies defined in the
KMP root module, KMP common module, Flutter Android module, Android App module, etc. Building against different
versions of the same dependency can cause a plethora of issues and should be avoided. Need to pull a dependency
from a private repository? You would not want to manually add the repository configuration and credentials to 
all the modules that need it. Don't repeat yourself. You also want to make sure the entire project uses the same
configuration: Single source of truth.

### Easy to use
Klutter should cater to the different technological layers and provide the configuration in a form that can be used easily.

Why? Once the SSOT principle is adhered too, it's just a matter of form.

### Easy to maintain
Any change in configuration should be propagated to all consumers.

Why? A change in the SSOT should update all dependens. If this is not guaranteed then there is no certainty that the
entire project is compliant, meaning dependency issues can arise or worse.


# Brief summary of usage
//TODO buildSrc is now used, explain
//TODO Secrets are loaded with Secrets class, explain