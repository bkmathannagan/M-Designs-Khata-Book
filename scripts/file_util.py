import os, sys

def save(path, data):
    p = os.path.normpath(path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w', encoding='utf-8') as f:
        f.write(data.strip() + '\n')
    print(f'Wrote {path}')
