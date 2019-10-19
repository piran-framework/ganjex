package com.twitter.crawler;

import com.sample.twitter.Analyzer;
import com.sample.twitter.AnalyzerMethod;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class MeTooHashTagAnalyzer implements Analyzer {
    @AnalyzerMethod
    public List<String> analyze() throws TwitterException {
        Twitter twitter = getTwitter();
        List<String> analyzedTweets = new ArrayList<String>();
        for (int i = 1; i < 2; i++) {
            ResponseList<Status> homeTimeline = twitter.getHomeTimeline(new Paging(i, 20));
            for (Status status : homeTimeline) {
                HashtagEntity[] hashtagEntities = status.getHashtagEntities();
                for (HashtagEntity hashtagEntity : hashtagEntities) {
                    if (hashtagEntity.getText().contains("MeToo")) {
                        analyzedTweets.add(status.getText());
                    }
                }
            }
        }
        return analyzedTweets;
    }

    private Twitter getTwitter() {
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
