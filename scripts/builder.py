import os

def w(path, content):
    p = os.path.normpath(path)
    os.makedirs(os.path.dirname(p) if os.path.dirname(p) else '.', exist_ok=True)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\n')
    print('Created ' + path)
