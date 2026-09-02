plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val moonlight_version: String by extra
val supplementaries_version: String by extra

dependencies {

    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")

    modCompileOnly("net.mehvahdjukaar:supplementaries-neoforge:${supplementaries_version}")

    modCompileOnly("curse.maven:farmers-delight-398521:5772720")
    modCompileOnly("curse.maven:entity-model-features-844662:8789172")
    modCompileOnly("curse.maven:entity-texture-features-fabric-568563:5000985")
}