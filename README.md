## Développement

Un exemple de plugin ajoutant des fonctionnalités est disponible ici : 
[trixcore-minecraft-example](https://github.com/TrixCMS-V-2/trixcore-minecraft-example)

La bibliothèque est disponible sur la plateforme [jitpack.io](https://jitpack.io/#eu.trixcms/trixcore-minecraft-common)

### Gradle

Ajoutez jipack.io dans les repositories de votre projet :

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Puis incluez **eu.trixcms:trixcore-minecraft-common** en dépendance :

```
dependencies {
     implementation 'eu.trixcms:trixcore-minecraft-common:v1.0.2'
}
```

### Maven

Ajoutez jipack.io dans les repositories de votre projet :

```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

Puis incluez **eu.trixcms:trixcore-minecraft-common** en dépendance :

```
<dependency>
	<groupId>eu.trixcms</groupId>
	<artifactId>trixcore-minecraft-common</artifactId>
	<version>v1.0.2</version>
</dependency>
```

## Builds

Pour buid les plugins :

> mvn clean install

et vous trouverez l'artifact dans le dossier target
