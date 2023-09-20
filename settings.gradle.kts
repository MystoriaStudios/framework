rootProject.name = "framework"

include("platform")
include("platform:paper")
include("platform:velocity")
include("platform:paper:api")
include("platform:paper:core")
include("platform:velocity:api")
include("platform:velocity:core")
include("platform:independent")
findProject(":platform:independent")?.name = "independent"
include("platform:independent:api")
findProject(":platform:independent:api")?.name = "api"
