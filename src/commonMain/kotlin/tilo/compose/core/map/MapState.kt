package tilo.compose.core.map

/**
 * Public name for mutable map state.
 *
 * The underlying implementation is still [Map] for compatibility. New public
 * API should prefer [MapState] to avoid confusion with the Compose map
 * composable.
 */
typealias MapState = Map
