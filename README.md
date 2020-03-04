# Fabric : Modèle mod client

## À modifier pour commencer

1. Dépendances (`build.gradle`) et versions (`gradle.properties`)
2. Package
    * Ctrl F + `__package__`
    * Renommer `src/main/java/neecko/__package__`
3. ID
    * Ctrl F + `__id__`
    * Renommer `src/main/resources/__id__.mixins.json`
    et `src/main/resources/assets/__id__`
4. Nom de classes
    * Ctrl F + `__name__`
    * Renommer `src/main/java/neecko/...`
5. Remplacer le `README.md`



## Petit tips

* Faire faire ses trucs à Gradle
    * Enregistrer `build.gradle` puis cliquer sur 'Yes'

* Sources
    * Génération : `./gradlew.bat genSources` (3-5 minutes)
    * Recherche d'un fichier : Ctrl P + `#MinecraftClient` (par exemple)

* Compiler
    1. `./gradlew.bat build`
    2. Résultat : `build/libs/[id]-[version].jar`



### Problèmes / Solutions

* `Failed to get sources`
    1. Ctrl Shift P + `> Java: Clean the language server workspace`
    2. `genSources`

* Pas d'autocomplete / de résultats dans Ctrl P
    * Actualiser Gradle



## Autres

Très inspiré de [fabric-example-mod](https://github.com/FabricMC/fabric-example-mod)
([CC0](https://github.com/FabricMC/fabric-example-mod/blob/master/LICENSE))
