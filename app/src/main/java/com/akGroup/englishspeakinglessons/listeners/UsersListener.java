package com.akGroup.englishspeakinglessons.listeners;

import com.akGroup.englishspeakinglessons.model.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);
}
