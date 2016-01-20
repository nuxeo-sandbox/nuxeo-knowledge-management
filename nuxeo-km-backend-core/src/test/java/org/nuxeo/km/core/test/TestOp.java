package org.nuxeo.km.core.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.km.operations.GetVote;
import org.nuxeo.km.operations.Vote;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

/**
 * Created by Michaël on 28/05/2015.
 */

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({"nuxeo-km-core"})
public class TestOp {

    @Inject
    CoreSession session;

    @Before
    public void studioConfigShouldBeLoaded() {
        @SuppressWarnings("unused")
        DocumentModelImpl input = (DocumentModelImpl) session.createDocumentModel("Vote");
    }

    @Ignore
    @Test
    public void testVote() throws Exception {

        DocumentModel doc = session.createDocumentModel("/","Article","Article");
        doc = session.createDocument(doc);

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(doc);
        OperationChain chain = new OperationChain("TestVote");
        chain.add(Vote.ID).
                set("vote", 3);
        as.run(ctx, chain);

    }


    @Ignore
    @Test
    public void testGetVote() throws Exception {

        DocumentModel doc = session.createDocumentModel("/","Article","Article");
        doc = session.createDocument(doc);

        DocumentModel vote = session.createDocumentModel("/CVS/VoteContainer","Vote","Vote");
        vote.setPropertyValue("vote:docId",doc.getId());
        vote.setPropertyValue("vote:username",session.getPrincipal().getName());
        vote.setPropertyValue("vote:vote",2);
        vote = session.createDocument(vote);

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(doc);
        OperationChain chain = new OperationChain("TestGetVote");
        chain.add(GetVote.ID);
        StringBlob results = (StringBlob) as.run(ctx, chain);

    }
}
