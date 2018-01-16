package com.destroystokyo.paper.profile;

import com.mojang.authlib.Agent;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;

public class PaperGameProfileRepository extends YggdrasilGameProfileRepository {
    public PaperGameProfileRepository(YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
        super.findProfilesByNames(names, agent, callback);
    }
}
