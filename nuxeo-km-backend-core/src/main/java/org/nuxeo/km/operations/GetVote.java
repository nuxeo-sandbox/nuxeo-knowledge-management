/**
 * 
 */

package org.nuxeo.km.operations;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

/**
 * @author mgena
 */
@Operation(id= GetVote.ID, category=Constants.CAT_DOCUMENT, label="Vote", description="")
public class GetVote {

    public static final String ID = "GetVote";

	@Context
	protected CoreSession session;

    @OperationMethod
    public Blob run(DocumentModel doc) {

		String username = session.getPrincipal().getName();

		String query = String.format(
				"Select * From Vote Where vote:docId = '%s' AND " +
						"vote:username = '%s'",doc.getId(),username);

		StringBlob results;

		DocumentModelList votes = session.query(query);

		if (votes!=null && votes.size()>0) {
			DocumentModel vote = votes.get(0);
			long rate = (long) vote.getPropertyValue("vote:vote");
			results = new StringBlob("{\"vote\":"+rate+"}");
		} else {
			results = new StringBlob("{\"vote\":"+0+"}");
		}

		return results;
    }    

}
