package net.mystoria.framework.security

import net.mystoria.framework.Framework

object SecurityService {

    fun configure(
        hashingAlgorithm: IHashingAlgorithm
    )
    {
        Framework.use {
            it.flavor.bind<IHashingAlgorithm>() to hashingAlgorithm
        }
    }
}