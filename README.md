You'll need to prepare the project before running by setting this value in `~/.sbt/local.sbt`:

```scala
SettingKey[File]("mongo-directory") := file("/usr/local/Cellar/mongodb/2.2.2-x86_64")
```
