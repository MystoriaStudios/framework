package net.revive.framework.security

import net.revive.framework.Framework

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