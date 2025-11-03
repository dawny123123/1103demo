#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Excel数据分析工具 - 完整版
结合markitdown和pandas进行Excel文件的全面分析
"""

import sys
import os
from pathlib import Path

# 添加虚拟环境的包路径
sys.path.insert(0, '/Users/yuxiao/Downloads/0713demo /myenv/lib/python3.13/site-packages')

from markitdown import MarkItDown
import pandas as pd
import warnings
warnings.filterwarnings('ignore')

class ComprehensiveExcelAnalyzer:
    def __init__(self):
        """初始化Excel分析器"""
        self.markitdown = MarkItDown()
        print("📊 Excel数据分析工具已初始化")
    
    def convert_to_markdown(self, excel_path):
        """使用MarkItDown将Excel转换为Markdown"""
        try:
            print(f"🔄 正在使用MarkItDown转换: {excel_path}")
            result = self.markitdown.convert(excel_path)
            return result.text_content
        except Exception as e:
            print(f"❌ MarkItDown转换失败: {str(e)}")
            return None
    
    def pandas_analysis(self, excel_path):
        """使用Pandas进行详细数据分析"""
        try:
            print(f"📈 正在使用Pandas分析: {excel_path}")
            
            # 读取Excel文件的所有工作表
            excel_file = pd.ExcelFile(excel_path)
            analysis_results = {}
            
            for sheet_name in excel_file.sheet_names:
                print(f"  📋 分析工作表: {sheet_name}")
                df = pd.read_excel(excel_path, sheet_name=sheet_name)
                
                # 基本信息
                sheet_analysis = {
                    '工作表名': sheet_name,
                    '数据形状': f"{df.shape[0]} 行, {df.shape[1]} 列",
                    '列名': list(df.columns),
                    '数据类型': df.dtypes.to_dict(),
                    '缺失值统计': df.isnull().sum().to_dict(),
                    '缺失值百分比': (df.isnull().sum() / len(df) * 100).round(2).to_dict()
                }
                
                # 数值列的统计信息
                numeric_columns = df.select_dtypes(include=['number']).columns
                if len(numeric_columns) > 0:
                    sheet_analysis['数值列统计'] = df[numeric_columns].describe().to_dict()
                
                # 文本列的统计信息
                text_columns = df.select_dtypes(include=['object']).columns
                if len(text_columns) > 0:
                    text_stats = {}
                    for col in text_columns:
                        unique_count = df[col].nunique()
                        most_common = df[col].value_counts().head(3).to_dict()
                        text_stats[col] = {
                            '唯一值数量': unique_count,
                            '最常见值': most_common
                        }
                    sheet_analysis['文本列统计'] = text_stats
                
                analysis_results[sheet_name] = sheet_analysis
            
            return analysis_results
            
        except Exception as e:
            print(f"❌ Pandas分析失败: {str(e)}")
            return None
    
    def generate_insights(self, pandas_results):
        """基于分析结果生成洞察"""
        insights = []
        
        for sheet_name, data in pandas_results.items():
            insights.append(f"## {sheet_name} 工作表洞察")
            
            # 基本洞察
            rows, cols = data['数据形状'].split(' 行, ')
            rows = int(rows)
            cols = int(cols.split(' 列')[0])
            
            insights.append(f"- 📊 包含 {rows} 条记录和 {cols} 个字段")
            
            # 缺失值洞察
            missing_data = data['缺失值统计']
            missing_cols = [col for col, count in missing_data.items() if count > 0]
            if missing_cols:
                insights.append(f"- ⚠️ 发现数据缺失: {', '.join(missing_cols)}")
            else:
                insights.append("- ✅ 数据完整，无缺失值")
            
            # 数值数据洞察
            if '数值列统计' in data:
                numeric_cols = list(data['数值列统计'].keys())
                insights.append(f"- 📈 数值字段: {', '.join(numeric_cols)}")
                
                # 找出变异系数最大的列（最不稳定的数据）
                for col in numeric_cols:
                    stats = data['数值列统计'][col]
                    if 'mean' in stats and 'std' in stats and stats['mean'] != 0:
                        cv = (stats['std'] / abs(stats['mean'])) * 100
                        if cv > 50:  # 变异系数大于50%
                            insights.append(f"  - 🎯 {col} 数据波动较大 (变异系数: {cv:.1f}%)")
            
            # 文本数据洞察
            if '文本列统计' in data:
                for col, stats in data['文本列统计'].items():
                    unique_count = stats['唯一值数量']
                    if unique_count == rows:
                        insights.append(f"  - 🔑 {col} 可能是唯一标识符")
                    elif unique_count < rows * 0.1:
                        insights.append(f"  - 📂 {col} 具有较少的分类值 ({unique_count} 种)")
        
        return '\n'.join(insights)
    
    def create_comprehensive_report(self, excel_path):
        """创建综合分析报告"""
        file_name = Path(excel_path).stem
        print(f"\n🎯 开始分析文件: {excel_path}")
        print("=" * 50)
        
        # MarkItDown分析
        markdown_content = self.convert_to_markdown(excel_path)
        
        # Pandas分析
        pandas_results = self.pandas_analysis(excel_path)
        
        # 生成洞察
        insights = ""
        if pandas_results:
            insights = self.generate_insights(pandas_results)
        
        # 创建完整报告
        report = f"""# Excel数据分析报告
## 文件信息
- **文件名**: {excel_path}
- **分析时间**: {pd.Timestamp.now().strftime('%Y-%m-%d %H:%M:%S')}

## 数据洞察
{insights}

## MarkItDown 转换结果
{markdown_content if markdown_content else "转换失败"}

## 详细统计信息
"""
        
        if pandas_results:
            for sheet_name, data in pandas_results.items():
                report += f"### {sheet_name} 详细信息\\n"
                report += f"- **形状**: {data['数据形状']}\\n"
                report += f"- **列名**: {', '.join(data['列名'])}\\n"
                
                if '数值列统计' in data:
                    report += "\\n**数值列统计**:\\n"
                    for col, stats in data['数值列统计'].items():
                        report += f"- {col}: 平均值={stats.get('mean', 'N/A'):.2f}, 标准差={stats.get('std', 'N/A'):.2f}\\n"
                
                report += "\\n"
        
        # 保存报告
        report_file = f"{file_name}_analysis_report.md"
        with open(report_file, 'w', encoding='utf-8') as f:
            f.write(report)
        
        print(f"📄 分析报告已保存: {report_file}")
        return report

def main():
    """主函数"""
    print("🚀 Excel数据分析工具启动")
    print("使用MarkItDown + Pandas进行综合分析")
    print("-" * 40)
    
    analyzer = ComprehensiveExcelAnalyzer()
    
    # 如果有命令行参数，使用参数作为文件路径
    if len(sys.argv) > 1:
        excel_file = sys.argv[1]
    else:
        # 检查是否有测试文件
        if os.path.exists('test_data.xlsx'):
            excel_file = 'test_data.xlsx'
            print(f"🎯 使用测试文件: {excel_file}")
        else:
            excel_file = input("📁 请输入Excel文件路径: ").strip()
    
    if not excel_file or not os.path.exists(excel_file):
        print("❌ 文件不存在或路径无效")
        return
    
    # 执行综合分析
    report = analyzer.create_comprehensive_report(excel_file)
    
    print("\\n" + "="*50)
    print("✅ 分析完成！")
    print("📋 报告摘要:")
    print("-" * 20)
    
    # 显示简要结果
    lines = report.split('\\n')[:20]  # 显示前20行
    for line in lines:
        if line.strip():
            print(line)
    
    print("\\n💡 查看完整报告请打开生成的markdown文件")

if __name__ == "__main__":
    main()