package com.moriatsushi.nav3.samples.nav

enum class OverlayType {
    FLOATING,
    FULLSCREEN,
    ;

    companion object {
        private const val OVERLAY_TYPE = "overlay_type"

        fun metadata(type: OverlayType): Map<String, Any> =
            mapOf(OVERLAY_TYPE to type)

        fun fromMetadata(metadata: Map<String, Any>): OverlayType? =
            metadata[OVERLAY_TYPE] as? OverlayType
    }
}
