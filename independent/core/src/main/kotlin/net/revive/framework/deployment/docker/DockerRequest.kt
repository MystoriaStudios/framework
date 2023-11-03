package net.revive.framework.deployment.docker

import com.github.dockerjava.transport.DockerHttpClient
import net.revive.framework.deployment.DeploymentService

/**
 * When handling a request, if the request does not need to
 * include parameters for identification or precise information
 * about our docker client we make it an object.
 *
 * Any other requests just use a constructor to call in
 * values that we can use in the main call method.
 *
 * Not the most clean method of doing this but it will work for what
 * we need it to.
 */
abstract class DockerRequest(
    val client: DockerHttpClient = DeploymentService.dockerClient
) {
    abstract fun call() : DockerHttpClient.Response
}