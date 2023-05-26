package ${package.Mapper};

import com.herenit.hiscloud.beans.domain.${entity};
import ${cfg.basePackage};

/**
* <p>
    * ${table.comment!} Mapper 接口
    * </p>
*
* @author ${author}
* @since ${date}
*/

public interface ${entity}Mapper extends ${cfg.basePackage?substring(cfg.basePackage?lastIndexOf(".")+1)}<${entity}Mapper,${entity}, ${entity}> {


}