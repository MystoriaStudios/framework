package net.revive.framework.updater.repository.repositories

import net.revive.framework.updater.repository.pagination.PaginatedRepositoryWrapper
import net.revive.framework.updater.repository.component.ComponentWrapper

/**
 * @author Dash
 * @since 1/21/2022
 */
class ComponentPaginatedRepositoryWrapper(
    override val items: MutableList<ComponentWrapper> = mutableListOf(),
    val continuationToken: String? = null
) : PaginatedRepositoryWrapper<ComponentWrapper>
