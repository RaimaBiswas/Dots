package com.dots.games.pratick.dots.Intro;

import com.dots.games.pratick.dots.R;

/**
 * Created by roypr on 05-04-2018.
 */

public enum IntroSlidesEnum {

    MAIN(IntroSlideFragment.newInstance(R.layout.intro_main)),
    SINGLE_PLAYER(IntroSlideFragment.newInstance(R.layout.intro_single_player_click)),
    SINGLE_PLAYER_START(IntroSlideFragment.newInstance(R.layout.intro_single_player_start_click)),
    GAME(IntroSlideFragment.newInstance(R.layout.intro_game)),
    GAME_END(IntroSlideFragment.newInstance(R.layout.intro_game_end)),
    SINGLE_PLAYER_AI(IntroSlideFragment.newInstance(R.layout.intro_single_player_ai_click)),
    SINGLE_PLAYER_SIZE(IntroSlideFragment.newInstance(R.layout.intro_single_player_size_click)),
    SIZE(IntroSlideFragment.newInstance(R.layout.intro_size)),
    SINGLE_PLAYER_STYLE(IntroSlideFragment.newInstance(R.layout.intro_single_player_style_click)),
    STYLE(IntroSlideFragment.newInstance(R.layout.intro_single_player_style_player_click)),
    SINGLE_PLAYER_COLOR(IntroSlideFragment.newInstance(R.layout.intro_single_player_style_player_color_click)),
    SINGLE_PLAYER_NAME(IntroSlideFragment.newInstance(R.layout.intro_single_player_style_player_name_click)),
    MULTI_PLAYER(IntroSlideFragment.newInstance(R.layout.intro_multi_player_click)),
    MULTI_PLAYER_PLAYERS(IntroSlideFragment.newInstance(R.layout.intro_multi_player_players_click)),
    MULTI_PLAYER_PLAY(IntroSlideFragment.newInstance(R.layout.intro_multi_player_players)),
    MIXED_PLAYER(IntroSlideFragment.newInstance(R.layout.intro_mixed_player_click)),
    MIXED_PLAYER_PLAYERS(IntroSlideFragment.newInstance(R.layout.intro_mixed_player_players_click)),
    MIXED_PLAYER_PLAY(IntroSlideFragment.newInstance(R.layout.intro_mixed_player_players)),
    ACHIEVEMENTS(IntroSlideFragment.newInstance(R.layout.intro_achievements_click)),
    ACHIEVEMENTS_SIGN_IN_OUT(IntroSlideFragment.newInstance(R.layout.intro_achievements_click_sign_in_out)),
    ACHIEVEMENTS_SHOW_ACHIEVEMENTS(IntroSlideFragment.newInstance(R.layout.intro_achievements_click_show_achievements)),
    ACHIEVEMENTS_SHOW_LEADER_BOARDS(IntroSlideFragment.newInstance(R.layout.intro_achievements_click_show_leaderboards));

    IntroSlideFragment introSlideFragment;
    IntroSlidesEnum(IntroSlideFragment introSlideFragment) {
        this.introSlideFragment = introSlideFragment;
    }
}
