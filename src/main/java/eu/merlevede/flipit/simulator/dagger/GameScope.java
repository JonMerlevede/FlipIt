package eu.merlevede.flipit.simulator.dagger;

/**
 * Created by Jonathan on 8/04/2016.
 */

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface GameScope {

}