plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val moonlight_version: String by extra
val supplementaries_version: String by extra
val polytone_version: String by extra

dependencies {

    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    modCompileOnly("net.mehvahdjukaar:polytone-common:${polytone_version}")

    modCompileOnly("net.mehvahdjukaar:supplementaries-common:${supplementaries_version}")

    modCompileOnly("curse.maven:farmers-delight-398521:5772720")
    modCompileOnly("maven.modrinth:entity-model-features:3.3.12-fabric-1.21")
    modCompileOnly("maven.modrinth:entitytexturefeatures:7.2.6-fabric-1.21")
    modCompileOnly("curse.maven:entity-texture-features-fabric-568563:5000985")
}