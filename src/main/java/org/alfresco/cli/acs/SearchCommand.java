package org.alfresco.cli.acs;

import org.alfresco.cli.format.FormatProvider;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.*;
import org.alfresco.search.sql.handler.SqlApi;
import org.alfresco.search.sql.model.SQLResultSetPagingList;
import org.alfresco.search.sql.model.SQLResultSetRowEntry;
import org.alfresco.search.sql.model.SQLSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;

@Component
@Command(name = "search", description = "Search commands")
public class SearchCommand {

    @Mixin
    FormatProviderRegistry formatProvider;

    @Autowired
    SearchApi searchApi;

    @Autowired
    private SqlApi sqlApi;

    @Command(description = "Execute cmis query.")
    public Integer cmis(@Parameters(description = "Query (example >> \"select * from cmis:folder\"") String query,
                        @Option(names = {"-pmi", "--paging-max-items"}, defaultValue = "100", description = "Max Items per Page") Integer pagingMaxItems,
                        @Option(names = {"-psc", "--paging-skip-count"}, defaultValue = "0", description = "Skip Count initial results") Integer pagingSkipCount) {
        ResultSetPagingList result =
                searchApi.search(new SearchRequest()
                        .query(new RequestQuery()
                                .language(RequestQuery.LanguageEnum.CMIS)
                                .query(query))
                        .paging(new RequestPagination()
                                .maxItems(pagingMaxItems)
                                .skipCount(pagingSkipCount))).getBody().getList();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Execute fts query.")
    public Integer fts(@Parameters(description = "Query (example >> \"cm:title:foo\")") String query,
                       @Option(names = {"-pmi", "--paging-max-items"}, defaultValue = "100", description = "Max Items per Page") Integer pagingMaxItems,
                       @Option(names = {"-psc", "--paging-skip-count"}, defaultValue = "0", description = "Skip Count initial results") Integer pagingSkipCount) {
        ResultSetPagingList result =
                searchApi.search(new SearchRequest()
                        .query(new RequestQuery()
                                .language(RequestQuery.LanguageEnum.AFTS)
                                .query(query))
                        .paging(new RequestPagination()
                                .maxItems(pagingMaxItems)
                                .skipCount(pagingSkipCount))).getBody().getList();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Execute sql query.")
    public Integer sql(@Parameters(description = "SQL Query (example >> \"select * from alfresco\")") String query) {
        SQLResultSetPagingList result = sqlApi.search(new SQLSearchRequest().stmt(query)).getBody().getList();
        formatProvider.print(result);
        return 0;
    }

    @Component
    static class ResultSetPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final ResultSetPagingList resultSetList = (ResultSetPagingList) item;
            List<ResultSetRowEntry> entries = resultSetList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-20s", "ID", "NAME", "TYPE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-40s %-20s", entry.getId(), entry.getName(), entry.getNodeType());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && ResultSetPagingList.class == itemClass;
        }
    }

    @Component
    static class SQLResultSetPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SQLResultSetPagingList resultSetList = (SQLResultSetPagingList) item;
            List<SQLResultSetRowEntry> entries = resultSetList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-80s", "FIELD", "VALUE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().forEach(entry -> {
                System.out.printf("%-40s %-80s", entry.getLabel(), entry.getValue());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SQLResultSetPagingList.class == itemClass;
        }
    }

}
