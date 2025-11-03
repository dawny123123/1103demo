#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MarkItDown MCP 客户端示例
展示如何与 markitdown-mcp 服务器交互
"""

import requests
import json
import sys
import os

class MarkItDownMCPClient:
    def __init__(self, base_url="http://127.0.0.1:3001"):
        """初始化MCP客户端"""
        self.base_url = base_url
        
    def check_server_status(self):
        """检查服务器状态"""
        try:
            response = requests.get(f"{self.base_url}/health", timeout=5)
            return response.status_code == 200
        except:
            return False
    
    def convert_document(self, file_path):
        """通过MCP服务器转换文档"""
        try:
            # 检查文件是否存在
            if not os.path.exists(file_path):
                print(f"❌ 文件不存在: {file_path}")
                return None
            
            print(f"🔄 通过MCP服务器转换文档: {file_path}")
            
            # 构造MCP请求
            mcp_request = {
                "method": "convert_document",
                "params": {
                    "file_path": file_path
                }
            }
            
            # 发送请求到MCP服务器
            response = requests.post(
                f"{self.base_url}/mcp",
                json=mcp_request,
                headers={"Content-Type": "application/json"},
                timeout=30
            )
            
            if response.status_code == 200:
                result = response.json()
                print("✅ 转换成功!")
                return result
            else:
                print(f"❌ 转换失败，状态码: {response.status_code}")
                print(f"错误信息: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 请求失败: {str(e)}")
            return None

def main():
    """主函数"""
    print("🚀 MarkItDown MCP 客户端示例")
    print("=" * 50)
    
    # 创建客户端
    client = MarkItDownMCPClient()
    
    # 检查服务器状态
    print("🔍 检查MCP服务器状态...")
    if not client.check_server_status():
        print("⚠️ MCP服务器未运行或无法连接")
        print("请先启动服务器: markitdown-mcp --http")
        print("或直接使用标准markitdown功能...")
        
        # 使用标准markitdown作为备选方案
        print("\n📋 使用标准MarkItDown进行转换")
        try:
            sys.path.insert(0, '/Users/yuxiao/Downloads/0713demo /myenv/lib/python3.13/site-packages')
            from markitdown import MarkItDown
            
            # 检查是否有测试文件
            test_file = "test_data.xlsx"
            if os.path.exists(test_file):
                print(f"🔄 转换文件: {test_file}")
                md = MarkItDown()
                result = md.convert(test_file)
                
                print("✅ 转换成功!")
                print("📋 结果:")
                print("-" * 30)
                print(result.text_content)
                
                # 保存结果
                output_file = f"{test_file}_mcp_result.md"
                with open(output_file, 'w', encoding='utf-8') as f:
                    f.write(f"# MarkItDown 转换结果\\n\\n")
                    f.write(f"源文件: {test_file}\\n\\n")
                    f.write(result.text_content)
                
                print(f"📄 结果已保存到: {output_file}")
            else:
                print(f"❌ 测试文件 {test_file} 不存在")
                
        except ImportError as e:
            print(f"❌ 导入MarkItDown失败: {str(e)}")
        except Exception as e:
            print(f"❌ 处理失败: {str(e)}")
        
        return
    
    print("✅ MCP服务器运行正常")
    
    # 获取要转换的文件
    if len(sys.argv) > 1:
        file_path = sys.argv[1]
    else:
        # 使用测试文件
        file_path = "test_data.xlsx"
    
    # 转换文档
    result = client.convert_document(file_path)
    
    if result:
        print("📋 转换结果:")
        print("-" * 30)
        print(json.dumps(result, indent=2, ensure_ascii=False))
    
    print("\n🎉 示例完成!")

if __name__ == "__main__":
    main()