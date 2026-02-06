package com.upiara.poc.generator;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

/** ORA-32575: Explicit column default is not supported for modifying vie hibernate **/
/** O Oracle não permite 'default' para auto-generate, e o Hibernate não permite null para ID logo usamos um dummy **/ 
public class DummyId extends IdentityGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
        return 1L;
    }
}
