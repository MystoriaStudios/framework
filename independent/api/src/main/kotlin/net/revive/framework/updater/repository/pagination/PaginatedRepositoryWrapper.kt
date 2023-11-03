package net.revive.framework.updater.repository.pagination

/**
 * @author Dash
 * @since 1/21/2022
 */
interface PaginatedRepositoryWrapper<T>
{
    val items: MutableList<T>
}
