package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import org.springframework.core.Ordered;

/**
 * Callback for customizing a {@link JpaEntityBuilder}.
 *
 * Invoked with an {@link org.springframework.core.Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 */
public interface JpaEntityCustomizer extends Ordered {

   void customize(JpaEntityBuilder jpaEntity);

    @Override
    default int getOrder() {
        return 0;
    }
}
