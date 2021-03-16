package org.alfresco.cli.acs;

import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestPagination;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetRowEntry;
import org.alfresco.search.model.SearchRequest;
import org.alfresco.search.sql.handler.SqlApi;
import org.alfresco.search.sql.model.SQLResultSetPaging;
import org.alfresco.search.sql.model.SQLResultSetPagingList;
import org.alfresco.search.sql.model.SQLResultSetRowEntry;
import org.alfresco.search.sql.model.SQLSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "search", mixinStandardHelpOptions = true, subcommands =
        {SearchCommand.CmisQueryCommand.class, SearchCommand.FtsQueryCommand.class, SearchCommand.SqlQueryCommand.class})
public class SearchCommand {

    static abstract class AbstractSearchCommand implements Callable<Integer> {

        @Autowired
        SearchApi searchApi;

        @Option(names = {"-q", "--query"}, required = true, description = "Query (examples >> \"select * from cmis:folder\", \"foo\")")
        String query;

        @CommandLine.ArgGroup(exclusive = false, multiplicity = "0..1", heading = "Paging Options")
        PagingOptions pagingOptions;

        static class PagingOptions {
            @Option(names = {"-pmi", "--paging-max-items"}, description = "Max Items per Page", required = true)
            private Integer pagingMaxItems;
            @Option(names = {"-psc", "--paging-skip-count"}, description = "Skip Count initial results", required = true)
            private Integer pagingSkipCount;
        }

        SearchRequest getSearchRequest(RequestQuery.LanguageEnum language) {
            SearchRequest searchRequest = new SearchRequest()
                    .query(new RequestQuery()
                            .language(language)
                            .query(query));
            if (pagingOptions != null) {
                searchRequest.paging(new RequestPagination()
                        .maxItems(pagingOptions.pagingMaxItems)
                        .skipCount(pagingOptions.pagingSkipCount));
            }
            return searchRequest;
        }
    }

    @Component
    @Command(name = "cmis", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class CmisQueryCommand extends AbstractSearchCommand {

        @Override
        public Integer call() {
            List<ResultSetRowEntry> result =
                    searchApi.search(getSearchRequest(RequestQuery.LanguageEnum.CMIS)).getBody().getList().getEntries();
            System.out.println(result);
            return result.size();
        }
    }

    @Component
    @Command(name = "fts", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class FtsQueryCommand extends AbstractSearchCommand {

        @Override
        public Integer call() throws Exception {
            List<ResultSetRowEntry> result =
                    searchApi.search(getSearchRequest(RequestQuery.LanguageEnum.AFTS)).getBody().getList().getEntries();
            System.out.println(result);
            return result.size();
        }
    }

    @Component
    @Command(name = "sql", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class SqlQueryCommand implements Callable<Integer> {

        @Autowired
        private SqlApi sqlApi;

        @Option(names = {"-q", "--query"}, required = true, description = "SQL Query (example >> \"select * from alfresco\")")
        private String query;

        @Override
        public Integer call() throws Exception {
            List<SQLResultSetRowEntry> result = sqlApi.search(new SQLSearchRequest().stmt(query)).getBody().getList().getEntries();
            System.out.println(result);
            return result.size();
        }
    }

}
