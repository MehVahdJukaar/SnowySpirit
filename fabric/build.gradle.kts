plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val supplementaries_version: String by extra
val codecui_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-fabric:${moonlight_version}")
    // JiJ'd into Moonlight, so not on the dev runtime classpath — add explicitly to avoid missing schema codec class.
    modRuntimeOnly("net.mehvahdjukaar:codecui-fabric:${codecui_version}")

    modCompileOnly("net.mehvahdjukaar:supplementaries-fabric:${supplementaries_version}") {
        // We already provide moonlight explicitly above; Supplementaries' metadata requests
        // moonlight-fabric with a `fabric` classifier that Loom can't resolve, so drop the transitive copy.
        exclude(group = "net.mehvahdjukaar", module = "moonlight-fabric")
    }

    modCompileOnly("curse.maven:yacl-667299:4574163")
    modCompileOnly("com.terraformersmc:modmenu:11.0.3")
    modCompileOnly("curse.maven:fabric-seasons-413523:4576886")
    modCompileOnly("curse.maven:farmers-delight-fabric-482834:4061213")
    modCompileOnly("curse.maven:entity-model-features-844662:8792251")
    modCompileOnly("curse.maven:entity-texture-features-fabric-568563:5000985")

    modRuntimeOnly("maven.modrinth:sodium:mc1.21.1-0.8.12-fabric")
}