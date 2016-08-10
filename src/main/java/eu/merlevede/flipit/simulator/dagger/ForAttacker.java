package eu.merlevede.flipit.simulator.dagger;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jonat on 18/04/2016.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ForAttacker {
}
