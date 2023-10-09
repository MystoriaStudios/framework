package net.revive.framework.security

object SecurityService {

    fun configure(
        hashingAlgorithm: IHashingAlgorithm
    )
    {
        net.revive.framework.Framework.use {
            it.flavor.bind<IHashingAlgorithm>() to hashingAlgorithm
        }
    }
}