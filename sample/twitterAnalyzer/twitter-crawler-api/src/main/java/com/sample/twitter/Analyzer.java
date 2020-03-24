package com.sample.twitter;

import java.util.List;

public abstract class Analyzer {
    List<String> analyze() throws Exception;

    protected Twitter getTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("XXXXXXXXXXXXX")
                .setOAuthConsumerSecret("XXXXXXXXXXXXX")
                .setOAuthAccessToken("XXXXXXXXXXXXX")
                .setOAuthAccessTokenSecret("XXXXXXXXXXXXX");
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }
}
