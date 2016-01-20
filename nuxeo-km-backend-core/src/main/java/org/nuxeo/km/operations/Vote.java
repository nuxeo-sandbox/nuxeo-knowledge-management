/**
 * 
 */

package org.nuxeo.km.operations;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * @author mgena
 */
@Operation(id= Vote.ID, category=Constants.CAT_DOCUMENT, label="Vote", description="")
public class Vote {

    public static final String ID = "Vote";

	@Context
	protected CoreSession session;

	@Param(name = "uservote")
	protected int uservote;

    @OperationMethod
    public DocumentModel run(DocumentModel doc) {

		String username = session.getPrincipal().getName();

		String query = String.format(
				"Select * From Vote Where vote:docId = '%s' AND " +
						"vote:username = '%s'",doc.getId(),username);

		DocumentModelList votes = session.query(query);

		if (votes!=null && votes.size()>0) {
			DocumentModel vote = votes.get(0);
			vote.setPropertyValue("vote:vote",uservote);
			session.saveDocument(vote);
		} else {
			DocumentModel vote = session.createDocumentModel("/CVS/VoteContainer","Vote","Vote");
			vote.setPropertyValue("vote:docId",doc.getId());
			vote.setPropertyValue("vote:docTitle",doc.getTitle());
			vote.setPropertyValue("vote:username",username);
			vote.setPropertyValue("vote:vote",uservote);
			session.createDocument(vote);
		}

		return doc;
    }    

}
