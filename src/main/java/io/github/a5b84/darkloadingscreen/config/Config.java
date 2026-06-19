package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.util.ARGB;

public class Config {

  /**
   * Factor to convert time from the config (in seconds) to the actual number of milliseconds to
   * use.
   */
  // (Dividing by 2 because the game waits twice as long)
  private static final float FADE_DURATION_FACTOR = LoadingOverlay.FADE_OUT_TIME / 2f;

  static final float MAX_FADE_DURATION = 5;

  // Colors
  public final int backgroundColor;
  public final int barColor;
  public final int barBackgroundColor;
  public final int barBorderColor;
  public final int logoColor;

  // RGB channels of some colors
  public final float backgroundRed;
  public final float backgroundGreen;
  public final float backgroundBlue;
  public final float logoRed;
  public final float logoGreen;
  public final float logoBlue;

  // Fade durations
  public final float fadeInDuration;
  public final float fadeOutDuration;
  public final float fadeInMillis;
  public final float fadeOutMillis;

  public static final Config DEFAULT =
      new Config(
          0x14181c,
          0xe22837,
          0x14181c,
          0x303336,
          0xffffff,
          LoadingOverlay.FADE_IN_TIME / FADE_DURATION_FACTOR,
          LoadingOverlay.FADE_OUT_TIME / FADE_DURATION_FACTOR);

  /**
   * @param fadeInDuration Fade in time in seconds
   * @param fadeOutDuration Fade out time in seconds
   */
  public Config(
      int backgroundColor,
      int barColor,
      int barBackgroundColor,
      int barBorderColor,
      int logoColor,
      float fadeInDuration,
      float fadeOutDuration) {
    this.backgroundColor = backgroundColor;
    this.barColor = barColor;
    this.barBackgroundColor = barBackgroundColor;
    this.barBorderColor = barBorderColor;
    this.logoColor = logoColor;
    this.fadeInDuration = Math.min(fadeInDuration, MAX_FADE_DURATION);
    this.fadeOutDuration = Math.min(fadeOutDuration, MAX_FADE_DURATION);

    backgroundRed = ARGB.redFloat(backgroundColor);
    backgroundGreen = ARGB.greenFloat(backgroundColor);
    backgroundBlue = ARGB.blueFloat(backgroundColor);
    logoRed = ARGB.redFloat(logoColor);
    logoGreen = ARGB.greenFloat(logoColor);
    logoBlue = ARGB.blueFloat(logoColor);

    fadeInMillis = fadeInDuration * FADE_DURATION_FACTOR;
    fadeOutMillis = fadeOutDuration * FADE_DURATION_FACTOR;
  }
}
