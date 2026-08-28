package gameapi.entity.data;

/**
 * DebugText 显示参数。
 * <p>
 * 仅 MOT 且客户端协议 >= 1.21.90 可用。
 *
 * @author glorydark
 */
public record DebugTextSettings(
        float scale,
        String fontColor,
        String backgroundColor,
        boolean depthTest,
        boolean useRotation,
        float rotationYaw,
        float rotationPitch,
        boolean showBackface,
        boolean showTextBackface,
        float duration
) {
    public static final DebugTextSettings DEFAULT = new DebugTextSettings(
            1.0f, "", "", true, false, 0f, 0f, true, true, 0f
    );
}
